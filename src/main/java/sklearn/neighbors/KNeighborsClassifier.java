/*
 * Copyright (c) 2016 Villu Ruusmann
 *
 * This file is part of JPMML-SkLearn
 *
 * JPMML-SkLearn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-SkLearn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-SkLearn.  If not, see <http://www.gnu.org/licenses/>.
 */
package sklearn.neighbors;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.dmg.pmml.CategoricalScoringMethodType;
import org.dmg.pmml.DataType;
import org.dmg.pmml.MiningFunctionType;
import org.dmg.pmml.NearestNeighborModel;
import org.jpmml.converter.Schema;
import org.jpmml.converter.ValueUtil;
import org.jpmml.sklearn.ClassDictUtil;
import sklearn.Classifier;

public class KNeighborsClassifier extends Classifier implements HasNeighbors, HasTrainingData {

	public KNeighborsClassifier(String module, String name){
		super(module, name);
	}

	@Override
	public int getNumberOfFeatures(){
		int[] shape = getFitXShape();

		return shape[1];
	}

	@Override
	public DataType getDataType(){
		return DataType.FLOAT;
	}

	@Override
	public NearestNeighborModel encodeModel(Schema schema){
		int[] shape = getFitXShape();

		int numberOfInstances = shape[0];
		int numberOfFeatures = shape[1];

		NearestNeighborModel nearestNeighborModel = KNeighborsUtil.encodeNeighbors(this, MiningFunctionType.CLASSIFICATION, numberOfInstances, numberOfFeatures, schema)
			.setCategoricalScoringMethod(CategoricalScoringMethodType.MAJORITY_VOTE);

		return nearestNeighborModel;
	}

	@Override
	public int getNumberOfNeighbors(){
		return ValueUtil.asInt((Number)get("n_neighbors"));
	}

	@Override
	public String getWeights(){
		return (String)get("weights");
	}

	@Override
	public String getMetric(){
		return (String)get("metric");
	}

	@Override
	public int getP(){
		return ValueUtil.asInt((Number)get("p"));
	}

	@Override
	public List<?> getY(){
		List<? extends Number> y = (List)ClassDictUtil.getArray(this, "_y");

		final
		List<?> classes = getClasses();

		Function<Number, Object> function = new Function<Number, Object>(){

			@Override
			public Object apply(Number number){
				int index = ValueUtil.asInt(number);

				return classes.get(index);
			}
		};

		return Lists.transform(y, function);
	}

	@Override
	public List<? extends Number> getFitX(){
		return (List)ClassDictUtil.getArray(this, "_fit_X");
	}

	private int[] getFitXShape(){
		return ClassDictUtil.getShape(this, "_fit_X", 2);
	}
}
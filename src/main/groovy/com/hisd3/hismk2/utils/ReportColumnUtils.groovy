package com.hisd3.hismk2.utils

import ar.com.fdvs.dj.domain.CustomExpression
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn

interface ColEval<T> {
    String evaluate(T value, Map fields, Map variables, Map parameters)
}

class ReportColumnUtils {

    //https://stackoverflow.com/questions/41566753/dynamicjasper-how-to-avoid-duplicate-values-in-reports-column
    static <T> AbstractColumn  createNonRepeatingColumnToString(Class<T> type,
                                                                String caption,
                                                                String field,
                                                                Integer width,
                                                                ColEval<T> evaluator){

        return ColumnBuilder.getNew()
                .setColumnProperty(field, type)
                .setTitle(caption)
                .setCustomExpression([
                        evaluate    : { Map fields, Map variables, Map parameters ->
                            def value = fields.get(field, null)
                            return  evaluator.evaluate(value,fields,variables,parameters)
                        },
                        getClassName: {
                            return String.class.name
                        }
                ] as CustomExpression)
                .setWidth(width)
                .setPrintRepeatedValues(false)
                .build()
    }


    static <T> AbstractColumn  createColumnToString(Class<T> type,
                                                    String caption,
                                                    String field,
                                                    Integer width,
                                                    ColEval<T> evaluator){

        return ColumnBuilder.getNew()
                .setColumnProperty(field, type)
                .setTitle(caption)
                .setCustomExpression([
                        evaluate    : { Map fields, Map variables, Map parameters ->
                            def value = fields.get(field, null)
                            return  evaluator.evaluate(value,fields,variables,parameters)
                        },
                        getClassName: {
                            return String.class.name
                        }
                ] as CustomExpression)
                .setCustomExpressionForCalculation([
                        evaluate    : { Map fields, Map variables, Map parameters ->
                            def value = fields.get(field, null)
                            return   value
                        },
                        getClassName: {
                            return type.name
                        }
                ] as CustomExpression)
                .setWidth(width)
                .build()
    }


}

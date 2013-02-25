/*
 * Copyright 2013 Maxim Kalina
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.docbook4j.eval;

import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

class MvelExpressionEvaluator implements ExpressionEvaluator {

    private static final Logger log = LoggerFactory
            .getLogger(MvelExpressionEvaluator.class);

    public String evaluate(String expression, Map<String, Object> context) {

        try {

            Object result = MVEL.eval(expression, context);
            if (result != null)
                return String.valueOf(result);

            return null;

        } catch (Exception e) {

            log.warn("Error parsing expression: " + expression, e);
            return expression;
        }

    }
}

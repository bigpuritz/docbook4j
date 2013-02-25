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

package com.google.code.docbook4j.renderer;

import com.google.code.docbook4j.Docbook4JException;

import java.io.InputStream;

public interface Renderer<T extends Renderer<T>> {

    T xml(String xmlResource);

    T xsl(String xslResource);

    T parameter(String name, String value);

    InputStream render() throws Docbook4JException;

}
/**
 * 该包将tz-eterm-interface-web中的服务封装成client,帮使用者完成了远程访问/序列化/反序列化的操作.
 * 该包既可能返回半结构化的数据,又可能返回结构化的数据.如果是半结构化的数据,需要结合
 * {@link com.travelzen.etermface.client.analysis}包中的工具类才能解开,从而返回结构化的数据.
 */
package com.travelzen.etermface.client.data;
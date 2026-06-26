(ns clojure.with-err-str)

(defmacro m [& body]
  `(let [s# (java.io.StringWriter.)]
     (binding [*err* s#]
       ~@body
       (str s#))))

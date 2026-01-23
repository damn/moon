(ns moon.ui-impl.error-window
  (:require [clojure.repl :as repl]
            [moon.ui :as ui]))

(defmacro ^:private with-err-str [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn create
  [{:keys [skin throwable]}]
  (ui/actor
   {:type :ui/window
    :title "Error"
    :rows [[{:actor (ui/actor
                     {:type :ui/label
                      :label/text (binding [*print-level* 3]
                                    (with-err-str
                                      (repl/pst throwable)))
                      :label/skin skin})}]]
    :modal? true
    :close-button? true
    :close-on-escape? true
    :center? true
    :skin skin
    :pack? true}))

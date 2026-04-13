(ns moon.actor-fns.error-window
  (:require [clojure.repl :as repl]
            [moon.ui :as ui]))

(defmacro ^:private with-err-str [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn create
  [{:keys [skin throwable]}]
  (let [label-text (binding [*print-level* 3]
                     (with-err-str
                       (repl/pst throwable)))]
    (ui/create {:type :ui/window
                :title "Error"
                :skin skin
                :window/close-button? skin
                :table/rows [[{:actor (ui/create {:type :ui/label
                                                  :text label-text
                                                  :skin skin})}]]
                :window/modal? true})))

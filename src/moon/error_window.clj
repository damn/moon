(ns moon.error-window
  (:require [clojure.repl :as repl]
            [gdx.actor.widget.label :as label]
            [gdx.actor.group.widget.table.window :as window]))

(defmacro with-err-str [& body]
  `(let [s# (java.io.StringWriter.)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn create
  [{:keys [skin throwable]}]
  (let [label-text (binding [*print-level* 3]
                     (with-err-str (repl/pst throwable)))]
    (doto (window/create {:title "Error"
                          :skin skin
                          :table/rows [[{:actor (label/create label-text skin)}]]
                          :window/add-close-button? true})
      (window/set-modal! true))))

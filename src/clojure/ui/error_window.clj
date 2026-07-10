(ns clojure.ui.error-window
  (:require 
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [clojure.ui.window.add-close-button :as add-close-button]
            [clojure.repl :as repl]))

(defmacro with-err-str [& body]
  `(let [s# (java.io.StringWriter.)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn create
  [{:keys [skin throwable]}]
  (let [label-text (binding [*print-level* 3]
                     (with-err-str (repl/pst throwable)))]
    (doto (doto (window/new "Error" skin)
    (table-set-opts/set-opts! {:title "Error"
            :skin skin
            :table/rows [[{:actor (label/new label-text skin)}]]}))
      (add-close-button/f! skin)
      (gdx-window/setModal true))))

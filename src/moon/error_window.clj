(ns moon.error-window
  (:require [clojure.repl :as repl]
            [clojure.gdx.scenes.scene2d.ui.table :as moon-table]
            [clojure.gdx.scenes.scene2d.ui.window :refer [add-close-button!]]
            [clojure.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]))

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
                (moon-table/set-opts! {:title "Error"
                                           :skin skin
                                           :table/rows [[{:actor (label/create label-text skin)}]]}))
          (add-close-button! skin)
          (gdx-window/setModal true))))

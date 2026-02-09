(ns moon.ui.error-window
  (:require [clojure.repl :as repl]
            [moon.ui.table :as table]
            [moon.ui.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin
                                               Window)))

(defmacro ^:private with-err-str [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn create
  [{:keys [^Skin skin throwable]}]
  (let [label-text (binding [*print-level* 3]
                     (with-err-str
                       (repl/pst throwable)))]
    (doto (Window. "Error" skin)
      (window/add-close-button! skin)
      (table/set-opts! {:rows [[{:actor (Label. ^String label-text
                                                ^Skin skin)}]]})
      (.setModal true)
      (.pack))))

(ns moon.error-window
  (:require [clojure.repl :as repl]
            [moon.table :as table]
            [moon.window :as window])
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
      (table/add-rows! [[{:actor (Label. ^String label-text ^Skin skin)}]])
      (.setModal true)
      (.pack))))

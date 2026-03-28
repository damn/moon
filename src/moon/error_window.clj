(ns moon.error-window
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [clojure.repl :as repl]
            [moon.table :as table]
            [moon.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
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
      (table/add-rows! [[{:actor (label/create label-text skin)}]])
      (.setModal true)
      (.pack))))

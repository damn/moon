(ns moon.error-window
  (:require [clojure.repl :as repl]
            [moon.scene2d.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label Skin)))

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
                          :table/rows [[{:actor (Label. ^String label-text ^Skin skin)}]]
                          :window/add-close-button? true})
      (window/set-modal! true))))

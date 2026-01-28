(ns moon.ui.error-window
  (:require [clojure.repl :as repl]
            [moon.ui.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defmacro ^:private with-err-str [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn create
  [{:keys [skin throwable]}]
  (doto (window/create
         {:title "Error"
          :rows [[{:actor (Label. ^String (binding [*print-level* 3]
                                            (with-err-str
                                              (repl/pst throwable)))
                                  ^Skin skin)}]]
          :skin skin})
    (.setModal true)
    (.pack)))

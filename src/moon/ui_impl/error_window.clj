(ns moon.ui-impl.error-window
  (:require [clojure.repl :as repl]
            [moon.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defmacro ^:private with-err-str [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn create
  [{:keys [skin throwable]}]
  (doto (ui/actor
         {:type :ui/window
          :title "Error"
          :rows [[{:actor (Label. ^String (binding [*print-level* 3]
                                            (with-err-str
                                              (repl/pst throwable)))
                                  ^Skin skin)}]]
          :modal? true
          :close-button? true
          :close-on-escape? true
          :center? true
          :skin skin})
    (.pack)))

(ns moon.ui.error-window
  (:require [clojure.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.window :as window]
            [clojure.repl :as repl]))

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
    (window/create
     {:title "Error"
      :skin skin
      :window/close-button? skin
      :table/rows [[{:actor (label/create
                             {:text label-text
                              :skin skin})}]]
      :window/modal? true})))

(ns game.ui.error-window
  (:require [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scenes.scene2d.ui.label :as label]
            [clojure.gdx.scenes.scene2d.ui.window :as window]
            [clojure.repl :as repl]))

(defmacro ^:private with-err-str [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defmethod actor/create :ui/error-window
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

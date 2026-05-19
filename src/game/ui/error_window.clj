(ns game.ui.error-window
  (:require [gdl.scene2d.actor :as actor]
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
    (actor/create {:type :ui/window
                   :title "Error"
                   :skin skin
                   :window/close-button? skin
                   :table/rows [[{:actor (actor/create {:type :ui/label
                                                        :text label-text
                                                        :skin skin})}]]
                   :window/modal? true})))

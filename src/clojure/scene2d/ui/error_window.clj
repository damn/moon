(ns clojure.scene2d.ui.error-window
  (:require [clojure.repl :as repl]
            [clojure.gdx.scene2d.actor :as actor]))

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
    (actor/create {:type :ui/window
                   :title "Error"
                   :skin skin
                   :window/close-button? skin
                   :table/rows [[{:actor (actor/create {:type :ui/label
                                                        :text label-text
                                                        :skin skin})}]]
                   :window/modal? true})))

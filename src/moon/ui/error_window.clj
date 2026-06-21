(ns moon.ui.error-window
  (:require [clojure.ui.label :as label]
            [gdx.scenes.scene2d.ui.window :as window]
            [clojure.window.set-modal :as set-modal]
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
    (doto (window/create
           {:title "Error"
            :skin skin
            :window/close-button? skin
            :table/rows [[{:actor (label/create
                                   {:text label-text
                                    :skin skin})}]]})
      (set-modal/f! true))))

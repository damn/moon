(ns clojure.ui.error-window
  (:require [com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [clojure.ui-label :as label]
            [clojure.ui-window :as window]
            [clojure.ui.window.add-close-button :as add-close-button]
            [clojure.repl :as repl]))

(defmacro with-err-str [& body]
  `(let [s# (java.io.StringWriter.)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn create
  [{:keys [skin throwable]}]
  (let [label-text (binding [*print-level* 3]
                     (with-err-str (repl/pst throwable)))]
    (doto (window/create
           {:title "Error"
            :skin skin
            :table/rows [[{:actor (label/create
                                   {:text label-text
                                    :skin skin})}]]})
      (add-close-button/f! skin)
      (gdx-window/setModal true))))

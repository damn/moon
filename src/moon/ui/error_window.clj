(ns moon.ui.error-window
  (:require [scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.ui.window.add-close-button :as add-close-button]
            [clojure.repl :as repl]
            [clojure.with-err-str :as with-err-str]))

(defn create
  [{:keys [skin throwable]}]
  (let [label-text (binding [*print-level* 3]
                     (with-err-str/m
                       (repl/pst throwable)))]
    (doto (window/create
           {:title "Error"
            :skin skin
            :table/rows [[{:actor (label/create
                                   {:text label-text
                                    :skin skin})}]]})
      (add-close-button/f! skin)
      (.setModal true))))

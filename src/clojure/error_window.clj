(ns clojure.error-window
  (:require [clojure.window :as gdx-window]
            [clojure.ui-label :as label]
            [clojure.ui-window :as window]
            [clojure.window.add-close-button :as add-close-button]
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
      (gdx-window/set-modal! true))))

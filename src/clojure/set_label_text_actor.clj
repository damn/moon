(ns clojure.set-label-text-actor
  (:require
            [gdl.actor :as actor]
            [gdl.label :as gdx-label]
            [clojure.scene2d-actor :as scene2d-actor]))

(defn set-label-text-actor [label text-fn]
  (scene2d-actor/f
   {:act! (fn [this _delta]
            (when-let [stage (actor/get-stage this)]
              (gdx-label/set-text! label (text-fn (:stage/ctx stage)))))}))

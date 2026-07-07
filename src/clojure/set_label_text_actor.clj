(ns clojure.set-label-text-actor
  (:require [clojure.actor :as gdx-actor]
            [clojure.label :as gdx-label]
            [clojure.scene2d-actor :as actor]))

(defn set-label-text-actor [label text-fn]
  (actor/f
   {:act! (fn [this _delta]
            (when-let [stage (gdx-actor/get-stage this)]
              (gdx-label/set-text! label (text-fn (:stage/ctx stage)))))}))

(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require [clojure.scene2d.actor.get-stage :refer [get-stage]]
            [clojure.scene2d.actor.create :as actor]
            [clojure.scene2d.ui.label :as label]))

(defn set-label-text-actor [label text-fn]
  (actor/create
   {:act! (fn [this _delta]
            (when-let [stage (get-stage this)]
              (label/set-text! label (text-fn (:stage/ctx stage)))))}))

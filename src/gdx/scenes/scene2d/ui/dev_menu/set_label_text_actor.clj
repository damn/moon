(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require [scene2d.actor.get-stage :as get-stage]
            [scene2d.actor :as actor]
            [ui.label :as label]))

(defn set-label-text-actor [label text-fn]
  (actor/f
   {:act! (fn [this _delta]
            (when-let [stage (get-stage/f this)]
              (label/set-text! label (text-fn (:stage/ctx stage)))))}))

(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require [clojure.gdx.actor.get-stage :as get-stage]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [scene2d.actor :as actor]))

(defn set-label-text-actor [label text-fn]
  (actor/f
   {:act! (fn [this _delta]
            (when-let [stage (get-stage/f this)]
              (label/set-text label (text-fn (:stage/ctx stage)))))}))

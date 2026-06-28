(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require [scene2d.actor :as actor]
            [scene2d.ui.label.set-text :as set-text!])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-label-text-actor [label text-fn]
  (actor/f
   {:act! (fn [this _delta]
            (when-let [stage (Actor/.getStage this)]
              (set-text!/f label (text-fn (:stage/ctx stage)))))}))

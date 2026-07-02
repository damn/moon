(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require [clojure.gdx.actor.get-stage :as get-stage]
            [scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label)))

(defn set-label-text-actor [label text-fn]
  (actor/f
   {:act! (fn [this _delta]
            (when-let [stage (get-stage/f this)]
              (Label/.setText label ^String (text-fn (:stage/ctx stage)))))}))

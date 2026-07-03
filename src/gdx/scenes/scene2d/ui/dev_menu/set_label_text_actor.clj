(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require [clojure.gdx.actor.get-stage :as get-stage]
            [clojure.gdx.label.set-text :as set-text]
            [scene2d.actor :as actor]))

(defn set-label-text-actor [label text-fn]
  (actor/f
   {:act! (fn [this _delta]
            (when-let [stage (get-stage/f this)]
              (set-text/f label (text-fn (:stage/ctx stage)))))}))

(ns clojure.set-label-text-actor
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as gdx-label]
            [clojure.scene2d-actor :as scene2d-actor]))

(defn set-label-text-actor [label text-fn]
  (scene2d-actor/f
   {:act! (fn [this _delta]
            (when-let [stage (actor/getStage this)]
              (gdx-label/setText label (text-fn (:stage/ctx stage)))))}))

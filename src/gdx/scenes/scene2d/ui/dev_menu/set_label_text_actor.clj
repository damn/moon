(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as gdx-actor]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as gdx-label]
            [scene2d.actor :as actor]))

(defn set-label-text-actor [label text-fn]
  (actor/f
   {:act! (fn [this _delta]
            (when-let [stage (gdx-actor/get-stage this)]
              (gdx-label/set-text! label (text-fn (:stage/ctx stage)))))}))

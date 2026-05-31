(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.ui.label :as label]))

(defn set-label-text-actor [label text-fn]
  (actor/create
   {:act! (fn [this _delta]
            (when-let [stage (actor/stage this)]
              (label/set-text! label (text-fn (:stage/ctx stage)))))}))

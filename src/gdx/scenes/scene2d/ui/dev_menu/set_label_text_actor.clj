(ns gdx.scenes.scene2d.ui.dev-menu.set-label-text-actor
  (:require [clojure.gdx :as gdx]
            [scene2d.actor :as actor]))

(defn set-label-text-actor [label text-fn]
  (actor/f
   {:act! (fn [this _delta]
            (when-let [stage (gdx/get-stage this)]
              (gdx/label-set-text! label ^String (text-fn (:stage/ctx stage)))))}))

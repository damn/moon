(ns editor.property-overview-window.table-rows
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [clojure.gdx.actor.set-touchable :as set-touchable]
            [clojure.gdx.event.get-stage :as get-stage]
            [clojure.gdx.group.add-actor :as add-actor]
            [clojure.gdx.image-button.new :as new-image-button]
            [clojure.gdx.texture-region.get-region-height :as get-region-height]
            [clojure.gdx.texture-region.get-region-width :as get-region-width]
            [clojure.gdx.texture-region-drawable.new :as new-texture-region-drawable]
            [clojure.gdx.texture-region-drawable.set-min-size :as set-min-size]
            [clojure.gdx.touchable.disabled :as touchable-disabled]
            [scene2d.ui.stack :as stack]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.label :as label]))

(defn overview-table-rows* [skin image-scale rows]
  (for [row rows]
    (for [{:keys [texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (let [stack (stack/create)]
                (run! #(add-actor/f stack %)
                      [(doto (new-image-button/f
                              (doto (new-texture-region-drawable/f texture-region)
                                (set-min-size/f (* image-scale (get-region-width/f texture-region))
                                                (* image-scale (get-region-height/f texture-region)))))
                        (add-listener/f (change-listener/create
                                         (fn [event actor]
                                           (on-clicked actor (:stage/ctx (get-stage/f event))))))
                        (add-listener/f (text-tooltip/create tooltip skin)))
                       (doto (label/create
                              {:text extra-info-text
                               :skin skin})
                         (set-touchable/f touchable-disabled/v))])
                stack)})))

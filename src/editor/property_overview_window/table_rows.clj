(ns editor.property-overview-window.table-rows
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
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
                (run! #(group/add-actor! stack %)
                      [(doto (image-button/new
                              (doto (texture-region-drawable/new texture-region)
                                (texture-region-drawable/set-min-size! (* image-scale (texture-region/get-region-width texture-region))
                                                (* image-scale (texture-region/get-region-height texture-region)))))
                        (actor/add-listener! (change-listener/create
                                         (fn [event actor]
                                           (on-clicked actor (:stage/ctx (event/get-stage event))))))
                        (actor/add-listener! (text-tooltip/create tooltip skin)))
                       (doto (label/create
                              {:text extra-info-text
                               :skin skin})
                         (actor/set-touchable! touchable/disabled))])
                stack)})))

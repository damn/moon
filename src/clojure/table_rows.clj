(ns clojure.table-rows
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [com.badlogic.gdx.scenes.scene2d.ui.stack :as stack]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]))

(defn overview-table-rows* [skin image-scale rows]
  (for [row rows]
    (for [{:keys [texture-region
                  on-clicked
                  tooltip
                  extra-info-text]} row]
      {:actor (let [stack (stack/new)]
                (run! #(group/addActor stack %)
                      [(doto (image-button/new
                              (doto (texture-region-drawable/new texture-region)
                                (texture-region-drawable/setMinSize (* image-scale (texture-region/getRegionWidth texture-region))
                                                (* image-scale (texture-region/getRegionHeight texture-region)))))
                        (actor/addListener (change-listener/create
                                         (fn [event actor]
                                           (on-clicked actor (:stage/ctx (event/getStage event))))))
                        (actor/addListener (text-tooltip/new tooltip skin)))
                       (doto (label/new extra-info-text skin)
                         (actor/setTouchable touchable/disabled))])
                stack)})))

(ns clojure.table-rows
  (:require
            [clojure.add-listener]
            [clojure.actor.set-touchable] [clojure.texture-region :as texture-region]
            [clojure.texture-region-drawable :as texture-region-drawable]
            [clojure.texture :as texture]
            [clojure.group :as group]
            [clojure.event :as event]
            [clojure.image-button :as image-button]
            [clojure.touchable :as touchable]
            [clojure.ui-stack :as stack]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.utils-change-listener :as change-listener]
            [clojure.ui-label :as label]))

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
                        (clojure.add-listener/f (change-listener/create
                                         (fn [event actor]
                                           (on-clicked actor (:stage/ctx (event/get-stage event))))))
                        (clojure.add-listener/f (text-tooltip/create tooltip skin)))
                       (doto (label/create
                              {:text extra-info-text
                               :skin skin})
                         (clojure.actor.set-touchable/f touchable/disabled))])
                stack)})))

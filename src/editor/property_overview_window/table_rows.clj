(ns editor.property-overview-window.table-rows
  (:require [clojure.gdx :as gdx]
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
                (run! #(gdx/add-actor! stack %)
                      [(doto (gdx/image-button
                              (doto (gdx/texture-region-drawable texture-region)
                                (gdx/texture-region-drawable-set-min-size!
                                 (* image-scale (gdx/texture-region-get-region-width texture-region))
                                 (* image-scale (gdx/texture-region-get-region-height texture-region)))))
                        (gdx/add-listener! (change-listener/create
                                            (fn [event actor]
                                              (on-clicked actor (:stage/ctx (gdx/event-get-stage event))))))
                        (gdx/add-listener! (text-tooltip/create tooltip skin)))
                       (doto (label/create
                              {:text extra-info-text
                               :skin skin})
                         (gdx/set-touchable! gdx/touchable-disabled))])
                stack)})))

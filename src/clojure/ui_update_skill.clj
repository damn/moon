(ns clojure.ui-update-skill
  (:require [clojure.group :as group]
            [clojure.action-bar-add-skill :as add-skill]
            [clojure.moon-textures :as textures]
            [clojure.info-entity :refer [info-text]]))

(defn f
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid skill]
  (-> stage
      :stage/root
      (#(group/find-actor % "moon.ui.action-bar"))
      (add-skill/f {:skill-id (:property/id skill)
                    :texture-region (textures/texture-region textures (:entity/image skill))
                    :tooltip-text (info-text skill ctx)}
                   skin))
  nil)

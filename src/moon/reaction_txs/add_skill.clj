(ns moon.reaction-txs.add-skill
  (:require [moon.info :as info]
            [moon.textures :as textures]
            [moon.ui.action-bar :as action-bar]
            [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn do!
  [{:keys [ctx/skin
           ctx/stage
           ctx/textures]
    :as ctx}
   eid skill]
  (when (:entity/player? @eid)
    (-> stage
        Stage/.getRoot
        (group/find-actor "moon.ui.action-bar")
        (action-bar/add-skill! {:skill-id (:property/id skill)
                                :texture-region (textures/texture-region textures (:entity/image skill))
                                :tooltip-text (info/text skill ctx)}
                               skin)))
  ctx)

#_(remove-skill! [stage skill-id]
                 (-> stage
                     .getRoot
                     (.findActor "moon.ui.action-bar")
                     (action-bar/remove-skill! skill-id)))

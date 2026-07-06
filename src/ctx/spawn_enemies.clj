(ns ctx.spawn-enemies
  (:require [ctx.do :refer [do!]]
            [moon.db.build :refer [build]]
            [gdx.maps.tiled.spawn-positions :as spawn-positions]))

(defn step
  [{:keys [ctx/db
           ctx/tiled-map]
    :as ctx}]
  (do! ctx
       (for [[position creature-id] (spawn-positions/f tiled-map)]
         [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                              :creature-property (build db (keyword creature-id))
                              :components {:entity/fsm {:fsm :fsms/npc
                                                        :initial-state :npc-sleeping}
                                           :entity/faction :evil}}])))

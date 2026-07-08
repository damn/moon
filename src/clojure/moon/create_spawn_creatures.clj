(ns clojure.moon.create-spawn-creatures
  (:require [clojure.build :refer [build]]
            [clojure.moon.ctx-do :refer [do!]]
            [clojure.spawn-positions :as spawn-positions]))

(defn f [ctx]
  (do! ctx
       (for [[position creature-id] (spawn-positions/f (:ctx/tiled-map ctx))]
         [:tx/spawn-creature {:position (mapv (partial + 0.5) position)
                              :creature-property (build (:ctx/db ctx) (keyword creature-id))
                              :components {:entity/fsm {:fsm :fsms/npc
                                                        :initial-state :npc-sleeping}
                                           :entity/faction :evil}}]))
  ctx)

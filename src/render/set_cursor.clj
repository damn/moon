(ns render.set-cursor
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.graphics :as graphics]
            entity.state.cursor.player-idle))

(def k->cursor
  {
   :player-item-on-cursor :cursors/hand-grab
   :player-dead :cursors/black-x
   :active-skill :cursors/sandclock
   :stunned :cursors/denied
   :player-moving :cursors/walking
   :player-idle entity.state.cursor.player-idle/f
   }
  )

(defn step
  [{:keys [ctx/app
           ctx/cursors
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        f (k->cursor state-k)
        cursor-key (if (keyword? f)
                     f
                     (f eid ctx))]
    (assert (contains? cursors cursor-key))
    (graphics/set-cursor! (app/graphics app) (get cursors cursor-key)))
  ctx)

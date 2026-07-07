(ns clojure.txs-fn-map
  (:require [clojure.state-exit :as state-exit]
            [clojure.tx-audiovisual :as tx-audiovisual]
            [clojure.assoc :as assoc]
            [clojure.assoc-in :as assoc-in]
            [clojure.dissoc :as dissoc]
            [clojure.tx-update :as tx-update]
            [clojure.mark-destroyed :as mark-destroyed]
            [clojure.set-cooldown :as set-cooldown]
            [clojure.add-text-effect :as add-text-effect]
            [clojure.tx-add-skill :as tx-add-skill]
            [clojure.tx-set-item :as tx-set-item]
            [clojure.tx-remove-item :as tx-remove-item]
            [clojure.pickup-item :as pickup-item]
            [clojure.tx-event :as tx-event]
            [clojure.unregister-eid :as unregister-eid]
            [clojure.state-enter :as state-enter]
            [clojure.effect :as effect]
            [clojure.spawn-alert :as spawn-alert]
            [clojure.spawn-line :as spawn-line]
            [clojure.move-entity :as move-entity]
            [clojure.spawn-projectile :as spawn-projectile]
            [clojure.spawn-effect :as spawn-effect]
            [clojure.spawn-item :as spawn-item]
            [clojure.spawn-creature :as spawn-creature]
            [clojure.spawn-entity :as spawn-entity]
            [clojure.tx-sound :as tx-sound]
            [clojure.toggle-inventory-visible :as toggle-inventory-visible]
            [clojure.ui-remove-item :as ui-remove-item]
            [clojure.ui-set-item :as ui-set-item]
            [clojure.ui-update-skill :as ui-update-skill]
            [clojure.show-message :as show-message]
            [clojure.show-modal :as show-modal]))

(def f
  {:tx/state-exit               state-exit/do!
   :tx/audiovisual              tx-audiovisual/do!
   :tx/assoc                    assoc/f
   :tx/assoc-in                 assoc-in/f
   :tx/dissoc                   dissoc/f
   :tx/update                   tx-update/f
   :tx/mark-destroyed           mark-destroyed/f
   :tx/set-cooldown             set-cooldown/do!
   :tx/add-text-effect          add-text-effect/do!
   :tx/add-skill                tx-add-skill/do!
   :tx/set-item                 tx-set-item/do!
   :tx/remove-item              tx-remove-item/do!
   :tx/pickup-item              pickup-item/do!
   :tx/event                    tx-event/do!
   :tx/unregister-eid           unregister-eid/do!
   :tx/state-enter              state-enter/do!
   :tx/effect                   effect/do!
   :tx/spawn-alert              spawn-alert/do!
   :tx/spawn-line               spawn-line/do!
   :tx/move-entity              move-entity/do!
   :tx/spawn-projectile         spawn-projectile/do!
   :tx/spawn-effect             spawn-effect/do!
   :tx/spawn-item               spawn-item/do!
   :tx/spawn-creature           spawn-creature/do!
   :tx/spawn-entity             spawn-entity/do!
   :tx/sound                    tx-sound/f
   :tx/toggle-inventory-visible toggle-inventory-visible/f
   :tx/ui-remove-item           ui-remove-item/f
   :tx/ui-set-item              ui-set-item/f
   :tx/ui-update-skill          ui-update-skill/f
   :tx/show-message             show-message/f
   :tx/show-modal               show-modal/f})

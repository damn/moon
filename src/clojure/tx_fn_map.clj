(ns clojure.tx-fn-map
  (:require [clojure.tx.state-exit :as state-exit]
            [clojure.tx.audiovisual :as audiovisual]
            [clojure.tx.assoc :as assoc]
            [clojure.tx.assoc-in :as assoc-in]
            [clojure.tx.dissoc :as dissoc]
            [clojure.tx.update :as update]
            [clojure.tx.mark-destroyed :as mark-destroyed]
            [clojure.tx.set-cooldown :as set-cooldown]
            [clojure.tx.add-text-effect :as add-text-effect]
            [clojure.tx.add-skill :as add-skill]
            [clojure.tx.set-item :as set-item]
            [clojure.tx.remove-item :as remove-item]
            [clojure.tx.pickup-item :as pickup-item]
            [clojure.tx.event :as event]
            [clojure.tx.unregister-eid :as unregister-eid]
            [clojure.tx.state-enter :as state-enter]
            [clojure.tx.effect :as effect]
            [clojure.tx.spawn-alert :as spawn-alert]
            [clojure.tx.spawn-line :as spawn-line]
            [clojure.tx.move-entity :as move-entity]
            [clojure.tx.spawn-projectile :as spawn-projectile]
            [clojure.tx.spawn-effect :as spawn-effect]
            [clojure.tx.spawn-item :as spawn-item]
            [clojure.tx.spawn-creature :as spawn-creature]
            [clojure.tx.spawn-entity :as spawn-entity]
            [clojure.tx.sound :as sound]
            [clojure.tx.toggle-inventory-visible :as toggle-inventory-visible]
            [clojure.tx.ui-remove-item :as ui-remove-item]
            [clojure.tx.ui-set-item :as ui-set-item]
            [clojure.tx.ui-update-skill :as ui-update-skill]
            [clojure.tx.show-message :as show-message]
            [clojure.tx.show-modal :as show-modal]))

(def f
  {:tx/state-exit               state-exit/do!
   :tx/audiovisual              audiovisual/do!
   :tx/assoc                    assoc/f
   :tx/assoc-in                 assoc-in/f
   :tx/dissoc                   dissoc/f
   :tx/update                   update/f
   :tx/mark-destroyed           mark-destroyed/f
   :tx/set-cooldown             set-cooldown/do!
   :tx/add-text-effect          add-text-effect/do!
   :tx/add-skill                add-skill/do!
   :tx/set-item                 set-item/do!
   :tx/remove-item              remove-item/do!
   :tx/pickup-item              pickup-item/do!
   :tx/event                    event/do!
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
   :tx/sound                    sound/f
   :tx/toggle-inventory-visible toggle-inventory-visible/f
   :tx/ui-remove-item           ui-remove-item/f
   :tx/ui-set-item              ui-set-item/f
   :tx/ui-update-skill          ui-update-skill/f
   :tx/show-message             show-message/f
   :tx/show-modal               show-modal/f})

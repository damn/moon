(ns clojure.update-labels.mouseover-entity-id)

(def item
  {:label "Mouseover-entity id"
   :update-fn (fn [{:keys [ctx/mouseover-eid]}]
                (when-let [entity (and mouseover-eid @mouseover-eid)]
                  (:entity/id entity)))
   :icon "images/mouseover.png"})

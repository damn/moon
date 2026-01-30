(ns moon.dev-menu.update-labels.mouseover-entity)

(def item
  {:label "Mouseover-entity id"
   :update-fn (fn [{:keys [ctx/mouseover-eid]}]
                (when-let [entity (and mouseover-eid @mouseover-eid)]
                  (:entity/id entity)))
   :icon "images/mouseover.png"})

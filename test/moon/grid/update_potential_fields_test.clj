(ns moon.grid.update-potential-fields-test)

(comment
 (clear-marked-cells! :good (get @faction->marked-cells :good))

 (defn- faction->tiles->entities-map* [entities]
   (into {}
         (for [[faction entities] (->> entities
                                       (filter   #(:entity/faction @%))
                                       (group-by #(:entity/faction @%)))]
           [faction
            (zipmap (map #(mapv int (:body/position (:entity/body @%))) entities)
                    entities)])))

 (def max-iterations 1)

 (let [entities (map ids->eids [140 110 91])
       tl->es (:good (faction->tiles->entities-map* entities))]
   tl->es
   (def last-marked-cells (generate-potential-field :good tl->es)))
 (println *1)
 (def marked *2)
 (step/f :good *1)
 )

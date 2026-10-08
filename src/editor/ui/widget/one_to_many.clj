(ns editor.ui.widget.one-to-many
  (:require [clojure.pprint :refer [pprint]]
            [editor.ui.overview :refer [property-overview-window]]
            [editor.ui.util :refer [find-ancestor]]
            [moon.db :as db]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Image Table TextButton TextTooltip Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Layout)))

(defn value [widget]
  (->> (.getChildren ^Group widget)
       (keep (fn [^Actor a] (.getUserObject a)))
       set))

(defn- add-one-to-many-rows [state table property-type property-ids]
  (let [{:keys [db skin stage textures]} @state
        redo-rows (fn [property-ids]
                    (.clearChildren ^Group table)
                    (add-one-to-many-rows state table property-type property-ids)
                    (.pack ^Layout (find-ancestor table (partial instance? Window))))]
    (doseq [row
     [[{:actor (doto (TextButton. "+" skin)
                 (.addListener (proxy [ChangeListener] []
                                 (changed [event _actor]
                                   (.addActor ^Stage stage
                                    (property-overview-window
                                     state
                                     property-type
                                     (fn [actor id]
                                       (.remove ^Actor (find-ancestor actor (partial instance? Window)))
                                       (redo-rows (conj property-ids id)))))))))}]
      (for [property-id property-ids]
        (let [property (db/get-raw db property-id)]
          {:actor (doto (Image. ^TextureRegion (textures/texture-region textures (or (:entity/image property)
                                                                                    (first (:animation/frames (:entity/animation property))))))
                    (.addListener (TextTooltip. ^String (binding [*print-level* 2]
                                                          (with-out-str
                                                            (pprint property))) skin))
                    (.setUserObject property-id))}))
      (for [id property-ids]
        {:actor (doto (TextButton. "-" skin)
                  (.addListener (proxy [ChangeListener] []
                                  (changed [event _actor]
                                    (redo-rows (disj property-ids id))))))})]]
      (doseq [cell row]
        (.add ^Table table ^Actor (:actor cell)))
      (.row ^Table table))))

(defn create [state [_ property-type] property-ids]
  (let [table (doto (Table.)
              (#(.pad (.defaults ^Table %) (float 5))))]
    (add-one-to-many-rows state table property-type property-ids)
    table))

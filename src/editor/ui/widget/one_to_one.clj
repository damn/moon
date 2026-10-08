(ns editor.ui.widget.one-to-one
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
       first))

(defn- add-one-to-one-rows [state table property-type property-id]
  (let [{:keys [db skin stage textures]} @state
        redo-rows (fn [id]
                    (.clearChildren ^Group table)
                    (add-one-to-one-rows state table property-type id)
                    (.pack ^Layout (find-ancestor table (partial instance? Window))))]
    (doseq [row (cond-> []
                  (not property-id)
                  (conj [{:actor (doto (TextButton. "+" skin)
                                   (.addListener (proxy [ChangeListener] []
                                                   (changed [event _actor]
                                                     (.addActor ^Stage stage
                                                      (property-overview-window
                                                       state
                                                       property-type
                                                       (fn [actor id]
                                                         (.remove ^Actor (find-ancestor actor (partial instance? Window)))
                                                         (redo-rows id))))))))}])
                  property-id
                  (conj [{:actor (let [property (db/get-raw db property-id)]
                                   (doto (Image. ^TextureRegion (textures/texture-region textures (or (:entity/image property)
                                                                                                     (first (:animation/frames (:entity/animation property))))))
                                     (.addListener (TextTooltip. ^String (binding [*print-level* 2]
                                                                           (with-out-str
                                                                             (pprint property))) skin))
                                     (.setUserObject property-id)))}]
                        [{:actor (doto (TextButton. "-" skin)
                                   (.addListener (proxy [ChangeListener] []
                                                   (changed [event _actor]
                                                     (redo-rows nil)))))}]))]
      (doseq [cell row]
        (.add ^Table table ^Actor (:actor cell)))
      (.row ^Table table))))

(defn create [state [_ property-type] property-id]
  (let [table (doto (Table.)
              (#(.pad (.defaults ^Table %) (float 5))))]
    (add-one-to-one-rows state table property-type property-id)
    table))

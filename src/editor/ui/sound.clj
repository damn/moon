(ns editor.ui.sound
  (:require [editor.ui.util :refer [find-ancestor]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane Table TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Layout)))

(defn- play-sound-button [state sound-name]
  (let [{:keys [play-sound! skin]} @state]
    (doto (TextButton. "play!" skin)
      (.addListener (proxy [ChangeListener] []
                      (changed [event _actor]
                        (play-sound! sound-name)))))))

(defn- sound-columns [state table sound-name open-select-sounds-handler]
  (let [{:keys [skin]} @state]
    [{:actor (doto (TextButton. sound-name skin)
               (.addListener (proxy [ChangeListener] []
                               (changed [event _actor]
                                 (open-select-sounds-handler table)))))}
     {:actor (play-sound-button state sound-name)}]))

(defn- rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor]
    (.clearChildren ^Group table)
    (doseq [cell (->sound-columns table sound-name)]
      (.add ^Table table ^Actor (:actor cell)))
    (.row ^Table table)
    (.remove ^Actor (find-ancestor actor (partial instance? Window)))
    (.pack ^Layout (find-ancestor table (partial instance? Window)))
    (let [[k _] (.getUserObject ^Actor table)]
      (.setUserObject ^Actor table [k sound-name]))))

(defn- choose-sound-button [state table sound-name ->sound-columns]
  (let [{:keys [skin]} @state]
    (doto (TextButton. sound-name skin)
      (.addListener (proxy [ChangeListener] []
                      (changed [event actor]
                        ((rebuild-sound-widget! table sound-name ->sound-columns) actor)))))))

(defn- list-sounds-table [pad rows]
  (let [^Table table (Table.)]
    (.pad (.defaults table) (float pad))
    (doseq [columns rows]
      (doseq [^Actor actor columns]
        (.add table actor))
      (.row table))
    (.pack table)
    table))

(defn- choose-sound-window [state table ->sound-columns]
  (let [{:keys [skin sound-names stage]} @state
        ^Table list-table (list-sounds-table
                           5
                           (for [sound-name sound-names]
                             [(choose-sound-button state table sound-name ->sound-columns)
                              (play-sound-button state sound-name)]))
        window (Window. "Choose" skin)
        c (.add window (ScrollPane. list-table skin))]
    (.width c (float (+ (.getWidth list-table) 50)))
    (.height c (float (min (- (.getWorldHeight (.getViewport ^Stage stage)) 50)
                            (.getHeight list-table))))
    (.row window)
    (.pack window)
    (.add (.getTitleTable window)
          (doto (TextButton. "X" skin)
            (.addListener (proxy [ChangeListener] []
                            (changed [_event _actor]
                              (.remove window))))))
    (.setModal window true)
    window))

(defn- open-select-sounds-handler [state table ->sound-columns]
  (fn []
    (.addActor ^Stage (:stage @state) (choose-sound-window state table ->sound-columns))))

(defn sound-widget [state sound-name]
  (let [{:keys [skin]} @state
        table (doto (Table.)
              (#(.pad (.defaults ^Table %) (float 5))))]
    (letfn [(sound-columns-fn [table sound-name]
              (sound-columns state table sound-name open-select-fn))
            (open-select-fn [table]
              (open-select-sounds-handler state table sound-columns-fn))]
      (doseq [cell (if sound-name
                    (sound-columns-fn table sound-name)
                    [{:actor
                      (doto (TextButton. "No sound" skin)
                        (.addListener (proxy [ChangeListener] []
                                        (changed [event _actor]
                                          ((open-select-fn table))))))}])]
        (.add ^Table table ^Actor (:actor cell)))
      (.row ^Table table)
      table)))

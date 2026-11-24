(ns clojure.com.badlogic.gdx.graphics.g2d.batch
  "A batch is used to draw 2D rectangles that reference a texture (region). The class will batch the drawing commands and
  optimize them for processing by the GPU.
  
  To draw something with a Batch one has to first call the [begin] method which will setup appropriate render
  states. When you are done with drawing you have to call [end] which will actually draw the things you specified.
  
  All drawing commands of the Batch operate in screen coordinates. The screen coordinate system has an x-axis pointing to the
  right, an y-axis pointing upwards and the origin is in the lower left corner of the screen. You can also provide your own
  transformation and projection matrices if you so wish.
  
  A batch is managed. In case the OpenGL context is lost all OpenGL resources a Batch uses internally get invalidated. A context
  is lost when a user switches to another application or receives an incoming call on Android. A batch will be automatically
  reloaded after the OpenGL context is restored.
  
  A batch is a pretty heavy object so you should only ever have one in your program.
  
  A batch works with OpenGL ES 2.0. It will use its own custom shader to draw all provided sprites. You can set your own custom
  shader via set shader.
  
  A batch has to be disposed if it is no longer used."
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d Batch TextureRegion)
           (com.badlogic.gdx.math Matrix4)))

(def X1 Batch/X1)
(def Y1 Batch/Y1)
(def C1 Batch/C1)
(def U1 Batch/U1)
(def V1 Batch/V1)
(def X2 Batch/X2)
(def Y2 Batch/Y2)
(def C2 Batch/C2)
(def U2 Batch/U2)
(def V2 Batch/V2)
(def X3 Batch/X3)
(def Y3 Batch/Y3)
(def C3 Batch/C3)
(def U3 Batch/U3)
(def V3 Batch/V3)
(def X4 Batch/X4)
(def Y4 Batch/Y4)
(def C4 Batch/C4)
(def U4 Batch/U4)
(def V4 Batch/V4)

(defn color
  "Returns the rendering color of this Batch. If the returned instance is manipulated, [set-color!] must be called
   afterward."
  [^Batch batch]
  (.getColor batch))

(defn set-color! [^Batch batch r g b a]
  (.setColor batch (float r) (float g) (float b) (float a)))

(defn begin
  "Sets up the Batch for drawing. This will disable depth buffer writing. It enables blending and texturing. If you have more
  texture units enabled than the first one you have to disable them before calling this. Uses a screen coordinate system by
  default where everything is given in pixels. You can specify your own projection and modelview matrices via
  [set-projection-matrix!] and set transform matrix."
  [^Batch batch]
  (.begin batch))

(defn end
  "Finishes off rendering. Enables depth writes, disables blending and texturing. Must always be called after a call to
  [begin]"
  [^Batch batch]
  (.end batch))

(defn set-projection-matrix!
  "Sets the projection matrix to be used by this Batch. If this is called inside a [begin]/[end] block, the
  current batch is flushed to the gpu."
  [^Batch batch ^Matrix4 matrix]
  (.setProjectionMatrix batch matrix))

(defn draw
  "Draws a rectangle using the given vertices. There must be 4 vertices, each made up of 5 elements in this order: x, y,
  color, u, v. The [color] from the Batch is not applied."
  ([^Batch batch ^Texture texture verts offset count]
   (.draw batch texture ^floats verts (int offset) (int count)))
  ([^Batch batch ^TextureRegion texture-region x y w h]
   (.draw batch texture-region (float x) (float y) (float w) (float h)))
  ([^Batch batch ^TextureRegion texture-region x y origin-x origin-y w h scale-x scale-y rotation]
   (.draw batch
          texture-region
          (float x)
          (float y)
          (float origin-x)
          (float origin-y)
          (float w)
          (float h)
          (float scale-x)
          (float scale-y)
          (float rotation))))

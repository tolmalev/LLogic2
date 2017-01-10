package ru.llogic.ui;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tolmalev
 */
public class GridUtilsTest {
    @Test
    public void toGrid() throws Exception {
        Assert.assertEquals(0, GridUtils.toGrid(4), 0.001);

        Assert.assertEquals(10, GridUtils.toGrid(5), 0.001);
        Assert.assertEquals(10, GridUtils.toGrid(6), 0.001);
        Assert.assertEquals(10, GridUtils.toGrid(7), 0.001);
        Assert.assertEquals(10, GridUtils.toGrid(8), 0.001);
        Assert.assertEquals(10, GridUtils.toGrid(9), 0.001);
        Assert.assertEquals(10, GridUtils.toGrid(10), 0.001);
        Assert.assertEquals(10, GridUtils.toGrid(11), 0.001);
        Assert.assertEquals(10, GridUtils.toGrid(12), 0.001);
        Assert.assertEquals(10, GridUtils.toGrid(13), 0.001);
        Assert.assertEquals(10, GridUtils.toGrid(14), 0.001);

        Assert.assertEquals(20, GridUtils.toGrid(15), 0.001);
        Assert.assertEquals(20, GridUtils.toGrid(16), 0.001);
        Assert.assertEquals(20, GridUtils.toGrid(17), 0.001);
        Assert.assertEquals(20, GridUtils.toGrid(18), 0.001);
        Assert.assertEquals(20, GridUtils.toGrid(19), 0.001);
        Assert.assertEquals(20, GridUtils.toGrid(20), 0.001);
    }

}